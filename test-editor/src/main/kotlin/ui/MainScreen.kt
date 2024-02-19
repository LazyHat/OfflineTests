package ui

import LocalMainViewModel
import Util.R
import Utils.BuildConfig
import Utils.Dialog
import Utils.increaseRGB
import Utils.svgPainterResource
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
import models.*
import theme.LazyTheme
import java.io.File


@Composable
fun MainScreen() {
    val vm = LocalMainViewModel.current
    val testState by vm.testStateAsState()
    val isOpened = testState is TestState.Opened
    val openedFile by vm.openedFileNameAsState()
    val dialogState by vm.collectDialogState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp).background(LazyTheme.colors.barOnBackground)
                    .padding(horizontal = 3.dp, vertical = 5.dp)
            ) {
                TopBarIconButton(
                    enabled = !isOpened,
                    onClick = { vm.newTest() },
                    resource = R.drawable.draft,
                )
                TopBarIconButton(
                    enabled = !isOpened,
                    onClick = { vm.openTest() },
                    resource = R.drawable.folder,
                )
                TopBarIconButton(
                    enabled = isOpened,
                    onClick = { vm.closeTest() },
                    resource = R.drawable.close,
                )
                Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(LazyTheme.colors.topBarIcons))
                TopBarIconButton(
                    enabled = isOpened && openedFile != null,
                    onClick = { vm.saveTest() },
                    resource = R.drawable.save,
                )
                TopBarIconButton(
                    enabled = isOpened,
                    onClick = { vm.saveTestToFile() },
                    resource = R.drawable.file_save,
                )
                Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(LazyTheme.colors.topBarIcons))
                TopBarIconButton(
                    enabled = openedFile != null && isOpened,
                    onClick = { vm.deleteTest() },
                    resource = R.drawable.delete_forever,
                )
                Spacer(modifier = Modifier.width(2.dp).fillMaxHeight().background(LazyTheme.colors.topBarIcons))
                Checkbox(
                    enabled = isOpened,
                    checked = true,
                    onCheckedChange = { vm.addQuestion(Question.SeveralAnswer.Default) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = LazyTheme.colors.topBarIcons,
                        uncheckedColor = LazyTheme.colors.topBarIcons,
                        checkmarkColor = LazyTheme.colors.background,
                        disabledColor = LazyTheme.colors.disabledTopBarIcons,
                        disabledIndeterminateColor = LazyTheme.colors.disabledTopBarIcons
                    )
                )
                RadioButton(
                    enabled = isOpened,
                    selected = true,
                    onClick = { vm.addQuestion(Question.SingleAnswer.Default) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = LazyTheme.colors.topBarIcons,
                        unselectedColor = LazyTheme.colors.topBarIcons,
                        disabledColor = LazyTheme.colors.disabledTopBarIcons
                    )
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth()
                    .background(LazyTheme.colors.barOnBackground),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("OS: ${BuildConfig.os}", color = LazyTheme.colors.onBackground)
                    Text("ver: ${BuildConfig.appVersion}", color = LazyTheme.colors.onBackground)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    (testState as? TestState.Opened)?.let {
                        Text("Questions: ${it.questions.size}", color = LazyTheme.colors.onBackground)
                    }
                    openedFile?.let {
                        Text("file: $it", color = LazyTheme.colors.onBackground)
                    }
                }
            }
        },
        backgroundColor = LazyTheme.colors.background
    ) {
        Box(contentAlignment = Alignment.Center) {

            when (testState) {
                is TestState.Opened -> testScreen(vm, testState as TestState.Opened)
                is TestState.Closed -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Test is not opened", fontSize = 20.sp, color = LazyTheme.colors.onBackground)
                            Spacer(Modifier.height(100.dp))
                            Button(
                                onClick = { vm.newTest() },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = LazyTheme.colors.primary,
                                    contentColor = LazyTheme.colors.onPrimary
                                )
                            ) {
                                Text("Create new Test")
                            }
                            Button(
                                onClick = { vm.openTest() },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = LazyTheme.colors.primary,
                                    contentColor = LazyTheme.colors.onPrimary
                                )
                            ) {
                                Text("Open test from file")
                            }
                        }
                    }
                }
            }
            Dialog(dialogState)
        }
    }
}


@Composable
fun testScreen(vm: MainViewModel, data: TestState.Opened) {

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
                onDelete = { vm.removeQuestion(index) },
                onChangeType = { vm.changeType(index) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TestInfoEdit(info: TestInfo, onEdit: (TestInfo) -> Unit) {
    var tempInfo by remember(info) { mutableStateOf(info) }

    LaunchedEffect(tempInfo) {
        delay(400)
        onEdit(tempInfo)
    }

    Card(
        modifier = Modifier.fillMaxWidth(0.7f),
        backgroundColor = LazyTheme.colors.questionBackground
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Title: ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LazyTheme.colors.onQuestionBackground
            )
            IndicatedTextField(
                tempInfo.title,
                { tempInfo = tempInfo.copy(title = it) },
                textStyle = TextStyle(fontSize = 18.sp, color = LazyTheme.colors.onQuestionBackground),
                indicatorColor = LazyTheme.colors.onQuestionBackground,
                backgroundColor = LazyTheme.colors.questionBackground.increaseRGB(0.1f),
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
    onChangeType: () -> Unit,
    onDelete: () -> Unit
) {
    var tempData by remember(data) { mutableStateOf(data) }

    LaunchedEffect(tempData) {
        delay(400)
        onEditData(tempData)
    }

    Card(
        modifier = Modifier.fillMaxWidth(0.7f).clickable(enabled = !editing) { onRequestEdit() },
        border = BorderStroke(3.dp, LazyTheme.colors.border).takeIf { editing },
        backgroundColor = LazyTheme.colors.questionBackground
    ) {
        Box {
            Row(Modifier.align(Alignment.TopEnd)) {
                when (tempData) {
                    is Question.SingleAnswer -> {
                        QuestionRadioButton(
                            enabled = editing,
                            selected = true,
                            onClick = onChangeType,
                        )
                    }

                    is Question.SeveralAnswer -> {
                        QuestionCheckbox(
                            enabled = editing,
                            checked = true,
                            onCheckedChange = { onChangeType() },
                        )
                    }
                }
                MyIconButton(
                    enabled = editing,
                    onClick = onDelete,
                    resource = R.drawable.close,
                    tint = Color.Red,
                    tintOnDisabled = LazyTheme.colors.disabledOnQuestionBackground
                )
            }
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                IndexedIndicatedTextField(
                    enabled = editing,
                    value = tempData.question,
                    index = index + 1,
                    onValueChange = { tempData = tempData.copyQuestion(question = it) },
                    indexPostfix = ".",
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LazyTheme.colors.onQuestionBackground
                    ),
                    onFocused = onRequestEdit,
                    indicatorColor = LazyTheme.colors.onQuestionBackground,
                    backgroundColor = LazyTheme.colors.questionBackground.increaseRGB(0.1f),
                    modifier = Modifier.padding(end = 80.dp)
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
                                    QuestionCheckbox(
                                        enabled = editing,
                                        checked = castedTempData.trueAnswers[index],
                                        onCheckedChange = {
                                            tempData = castedTempData.changeTrueAnswer(index)
                                        }
                                    )
                                }

                                is Question.SingleAnswer -> {
                                    val castedTempData = tempData as Question.SingleAnswer
                                    QuestionRadioButton(
                                        enabled = editing,
                                        selected = castedTempData.trueAnswerIndex == index,
                                        onClick = {
                                            tempData = castedTempData.editTrueAnswerIndex(index)
                                        }
                                    )
                                }
                            }

                            IndexedIndicatedTextField(
                                enabled = editing,
                                value = it,
                                index = index + 1,
                                onValueChange = {
                                    tempData = tempData.editAnswer(index, it)
                                },
                                indexPostfix = ".",
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                                onFocused = onRequestEdit,
                                indicatorColor = LazyTheme.colors.onQuestionBackground,
                                backgroundColor = LazyTheme.colors.questionBackground.increaseRGB(0.1f)
                            )

                            MyIconButton(
                                enabled = editing,
                                resource = R.drawable.delete_forever,
                                tintOnDisabled = Color.Transparent,
                                tint = LazyTheme.colors.onQuestionBackground,
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
                        Button(
                            onClick = {
                                tempData = tempData.addAnswer("Answer ${tempData.answers.size + 1}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = LazyTheme.colors.primary,
                                contentColor = LazyTheme.colors.onPrimary
                            )
                        ) {
                            Text("Add answer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCheckbox(enabled: Boolean, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Checkbox(
        enabled = enabled,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = LazyTheme.colors.onQuestionBackground,
            uncheckedColor = LazyTheme.colors.onQuestionBackground,
            checkmarkColor = LazyTheme.colors.questionBackground,
            disabledColor = LazyTheme.colors.disabledOnQuestionBackground
        )
    )
}

@Composable
fun QuestionRadioButton(enabled: Boolean, selected: Boolean, onClick: () -> Unit) {
    RadioButton(
        enabled = enabled,
        selected = selected,
        onClick = onClick,
        colors = RadioButtonDefaults.colors(
            selectedColor = LazyTheme.colors.onQuestionBackground,
            unselectedColor = LazyTheme.colors.onQuestionBackground,
            disabledColor = LazyTheme.colors.disabledOnQuestionBackground
        )
    )
}

@Composable
fun TopBarIconButton(
    enabled: Boolean,
    onClick: () -> Unit,
    resource: File,
    modifier: Modifier = Modifier
) {
    MyIconButton(
        enabled,
        onClick,
        resource,
        LazyTheme.colors.topBarIcons,
        LazyTheme.colors.disabledTopBarIcons,
        modifier
    )
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
    indicatorColor: Color = LazyTheme.colors.onBackground,
    backgroundColor: Color = LazyTheme.colors.background,
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
        Column(Modifier.padding(5.dp).background(if (enabled) backgroundColor else Color.Transparent)) {
            it.invoke()
            Box(
                Modifier.background(color = if (focusedState && enabled) indicatorColor else Color.Transparent)
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
    indicatorColor: Color = LazyTheme.colors.onBackground,
    backgroundColor: Color = LazyTheme.colors.background,
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
            onFocused = onFocused,
            indicatorColor = indicatorColor,
            backgroundColor = backgroundColor
        )
    }
}


