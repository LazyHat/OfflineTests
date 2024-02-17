package ui

import LocalBuildConfig
import LocalMainViewModel
import Util.Dialog
import Util.R
import Util.svgPainterResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import models.DialogWindow
import models.Question
import models.TestInfo
import models.TestState
import java.io.File


@Composable
fun MainScreen() {
    val buildConfig = LocalBuildConfig.current
    val vm = LocalMainViewModel.current
    val testState by vm.testStateAsState()
    val isOpened = testState is TestState.Opened
    val dialogWindow by vm.dialogWindowStateAsState()
    val openedFile by vm.openedFileNameAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.Gray)
                    .padding(horizontal = 3.dp, vertical = 5.dp)
            ) {
                MyIconButton(
                    enabled = !isOpened,
                    onClick = { vm.newTest() },
                    resource = R.drawable.draft
                )
                MyIconButton(
                    enabled = !isOpened,
                    onClick = { vm.openTest() },
                    resource = R.drawable.folder
                )
                MyIconButton(
                    enabled = isOpened,
                    onClick = { vm.closeTest() },
                    resource = R.drawable.close
                )
                Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
                MyIconButton(
                    enabled = isOpened && openedFile != null,
                    onClick = { vm.saveTest() },
                    resource = R.drawable.save
                )
                MyIconButton(
                    enabled = isOpened,
                    onClick = { vm.saveTestToFile() },
                    resource = R.drawable.file_save
                )
                Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
                MyIconButton(
                    enabled = openedFile != null && isOpened,
                    onClick = { vm.deleteTest() },
                    resource = R.drawable.delete_forever
                )
                Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
                Checkbox(
                    enabled = isOpened,
                    checked = true,
                    onCheckedChange = { vm.addQuestion(Question.SeveralAnswer.Default) })
                RadioButton(
                    enabled = isOpened,
                    selected = true,
                    onClick = { vm.addQuestion(Question.SingleAnswer.Default) })
            }
        },
        bottomBar = {
            Row(modifier = Modifier.padding(2.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("OS: ${buildConfig.os}")
                    Text("ver: ${buildConfig.appVersion}")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    (testState as? TestState.Opened)?.let {
                        Text("Questions: ${it.questions.size}")
                    }
                    openedFile?.let {
                        Text("file: $it")
                    }
                }
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {

            when (testState) {
                is TestState.Opened -> testScreen(vm, testState as TestState.Opened)
                is TestState.Closed -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Test is not opened", fontSize = 20.sp)
                            Button({ vm.newTest() }) {
                                Text("Create new Test")
                            }
                        }
                    }
                }
            }
            dialogWindow.let {
                if (it is DialogWindow.Opened)
                    Dialog(it.question, it.text1, it.text2, it.text3, it.action1, it.action2, it.action3)
            }
        }
    }
}


@Composable
fun testScreen(vm: MainViewModel, data: TestState.Opened) {

//    val windowKeyEventHolder = LocalWindowKeyEventHolder.current

    LazyColumn(
        modifier = Modifier.padding(20.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TestInfoEdit(data.testInfo) { vm.editTestInfo(it) }
        }
        itemsIndexed(data.questions) { index, it1 ->
            Spacer(modifier = Modifier.height(10.dp))
            AnswerEditQuestion(
                index = index,
                data = it1.question,
                editing = it1.editing,
                onRequestEdit = { vm.requestEdit(index) },
                onEditData = { vm.editQuestion(index, it) },
                onDelete = { vm.removeQuestion(index) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TestInfoEdit(info: TestInfo, onEdit: (TestInfo) -> Unit) {
    var tempInfo by remember(info) { mutableStateOf(info) }

    LaunchedEffect(tempInfo) {
        delay(100)
        onEdit(tempInfo)
    }

    Card(
        modifier = Modifier.defaultMinSize(minWidth = 500.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Title: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IndicatedTextField(
                tempInfo.title,
                { tempInfo = tempInfo.copy(title = it) },
                textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            )
        }
    }
}

@Composable
fun AnswerEditQuestion(
    index: Int,
    data: Question,
    editing: Boolean,
    onRequestEdit: () -> Unit,
    onEditData: (Question) -> Unit,
    onDelete: () -> Unit
) {
    var tempData by remember(data) { mutableStateOf(data) }

    LaunchedEffect(tempData) {
        delay(100)
        onEditData(tempData)
    }

    Card(
        modifier = Modifier.defaultMinSize(minWidth = 500.dp).clickable(enabled = !editing) { onRequestEdit() },
        border = BorderStroke(3.dp, Color.Gray).takeIf { editing }
    ) {
        Box {
            MyIconButton(
                enabled = editing,
                onClick = onDelete,
                resource = R.drawable.close,
                tint = Color.Red,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                IndexedIndicatedTextField(
                    value = tempData.question,
                    index = index + 1,
                    onValueChange = { tempData = tempData.copyQuestion(question = it) },
                    indexPostfix = ".",
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    onFocused = onRequestEdit
                )

                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(start = 15.dp).animateContentSize()) {
                    tempData.answers.forEachIndexed { index, it ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            when (tempData) {
                                is Question.SeveralAnswer -> {
                                    val castedTempData = tempData as Question.SeveralAnswer
                                    Checkbox(
                                        enabled = editing,
                                        checked = castedTempData.trueAnswers[index],
                                        onCheckedChange = {
                                            tempData = castedTempData.changeTrueAnswer(index)
                                        }
                                    )
                                }

                                is Question.SingleAnswer -> {
                                    val castedTempData = tempData as Question.SingleAnswer
                                    RadioButton(
                                        enabled = editing,
                                        selected = castedTempData.trueAnswerIndex == index,
                                        onClick = {
                                            tempData = castedTempData.editTrueAnswerIndex(index)
                                        }
                                    )
                                }
                            }

                            IndexedIndicatedTextField(
                                value = it,
                                index = index + 1,
                                onValueChange = {
                                    tempData = tempData.editAnswer(index, it)
                                },
                                indexPostfix = ".",
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                                onFocused = onRequestEdit
                            )

                            MyIconButton(
                                enabled = editing,
                                resource = R.drawable.delete_forever,
                                tintOnDisabled = Color.Transparent,
                                onClick = {
                                    tempData = tempData.removeAnswer(index)
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    AnimatedVisibility(
                        editing,
                        enter = expandVertically(tween(300, 0, LinearEasing)) { it },
                        exit = shrinkVertically(tween(300, 0, LinearEasing)) { it })
                    {
                        Button(onClick = {
                            tempData = tempData.addAnswer("Answer ${tempData.answers.size + 1}")
                        }) {
                            Text("Add answer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyIconButton(
    enabled: Boolean,
    onClick: () -> Unit,
    resource: File,
    tint: Color = MaterialTheme.colors.onBackground,
    tintOnDisabled: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
    modifier: Modifier = Modifier
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            svgPainterResource(resource),
            null,
            colorFilter = ColorFilter.tint(if (enabled) tint else tintOnDisabled)
        )
    }
}

@Composable
fun IndicatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    onFocused: () -> Unit = {}
) {

    val interactionSource = remember { MutableInteractionSource() }
    val focusedState by interactionSource.collectIsFocusedAsState()

    val density = LocalDensity.current
    var width = 0.dp

    LaunchedEffect(focusedState) {
        if (focusedState)
            onFocused()
    }

    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        textStyle = textStyle,
        cursorBrush = SolidColor(Color.White),
        interactionSource = interactionSource,
        modifier = modifier.onGloballyPositioned {
            with(density) { width = it.size.width.toDp() }
        },
        enabled = enabled
    ) {
        Column(Modifier.padding(5.dp).background(Color.Gray.copy(alpha = 0.3f))) {
            it.invoke()
            Box(
                Modifier.background(color = if (focusedState && enabled) MaterialTheme.colors.secondary else Color.Transparent)
                    .height(2.dp).width(width)
            )
        }
    }
}

@Composable
fun IndexedIndicatedTextField(
    value: String,
    index: Int,
    onValueChange: (String) -> Unit,
    indexPostfix: String? = null,
    indexSpacing: Dp = 10.dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    onFocused: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "${index}${indexPostfix.orEmpty()}",
            fontSize = textStyle.fontSize,
            color = textStyle.color,
            fontWeight = textStyle.fontWeight,
            fontFamily = textStyle.fontFamily
        )

        Spacer(modifier = Modifier.width(indexSpacing))

        IndicatedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            enabled = enabled,
            onFocused = onFocused
        )
    }
}


