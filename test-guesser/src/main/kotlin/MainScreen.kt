import Utils.BuildConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import models.Question
import models.checkIfOneAnswerSetted
import theme.LazyTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val vm = LocalMainViewModel.current
    val test by vm.collectTestStateAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
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
                }
            }
        },
        backgroundColor = LazyTheme.colors.background
    ) {

        test.let { openedTest ->

            when (openedTest) {
                is TestState.Closed -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Test Guesser",
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                color = LazyTheme.colors.onBackground
                            )
                            Spacer(Modifier.height(30.dp))
                            Button(
                                onClick = { vm.openTest() },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = LazyTheme.colors.primary,
                                    contentColor = LazyTheme.colors.onPrimary
                                )
                            ) {
                                Text("Open test")
                            }
                        }
                    }
                }

                is TestState.Opened -> {
                    val pagerState = rememberPagerState { openedTest.test.questions.size }
                    Scaffold(
                        topBar = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Card(
                                    shape = RoundedCornerShape(5.dp),
                                    backgroundColor = LazyTheme.colors.barOnBackground,
                                    contentColor = LazyTheme.colors.onBackground
                                ) {
                                    LazyVerticalGrid(
                                        GridCells.FixedSize(50.dp),
                                        modifier = Modifier.padding(5.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        itemsIndexed(openedTest.test.questions) { index, it ->
                                            OutlinedButton(
                                                shape = RoundedCornerShape(4.dp),
                                                onClick = {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(index)
                                                    }
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    backgroundColor = LazyTheme.colors.barOnBackground,
                                                    LazyTheme.colors.onBackground
                                                ),
                                                border = BorderStroke(
                                                    2.dp,
                                                    if (it.checkIfOneAnswerSetted()) LazyTheme.colors.primary else LazyTheme.colors.border
                                                )
                                            ) {
                                                Text("${index + 1}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                AnimatedVisibility(openedTest.test.questions.all { it.checkIfOneAnswerSetted() }) {
                                    Button(
                                        onClick = {vm.endTest()},
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = LazyTheme.colors.primary,
                                            contentColor = LazyTheme.colors.onPrimary
                                        )
                                    ) {
                                        Text("End test")
                                    }
                                }
                            }
                        },
                        backgroundColor = LazyTheme.colors.background,
                        modifier = Modifier.padding(it)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            userScrollEnabled = false,
                            modifier = Modifier.padding(it)
                        ) { page ->
                            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                                Box(Modifier.align(Alignment.Center)) {
                                    AnswerEditQuestion(page, openedTest.test.questions[page]) { vm.changeAnswer(page, it) }
                                }
                                if (page != 0)
                                    Button(
                                        modifier = Modifier.align(Alignment.BottomStart),
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(page - 1)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = LazyTheme.colors.primary,
                                            contentColor = LazyTheme.colors.onPrimary
                                        )
                                    ) {
                                        Text("Back")
                                    }
                                if (page != openedTest.test.questions.size - 1)
                                    Button(
                                        modifier = Modifier.align(Alignment.BottomEnd),
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(page + 1)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = LazyTheme.colors.primary,
                                            contentColor = LazyTheme.colors.onPrimary
                                        )
                                    ) {
                                        Text("Next")
                                    }
                            }
                        }
                    }
                }

                is TestState.Ended -> {
                    Box(
                        Modifier.fillMaxSize().background(LazyTheme.colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Test Ended",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = LazyTheme.colors.onBackground
                            )
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "${openedTest.results.correctAnswers}/${openedTest.results.totalAnswers}",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = LazyTheme.colors.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerEditQuestion(
    index: Int,
    data: Question,
    onChangeAnswer: (Int) -> Unit
) {
    Card(
        modifier = Modifier.defaultMinSize(minWidth = 500.dp),
        border = BorderStroke(3.dp, Color.Gray),
        backgroundColor = LazyTheme.colors.questionBackground,
        contentColor = LazyTheme.colors.onQuestionBackground
    ) {
        Box(Modifier.padding(20.dp)) {
            Column {
                Text(
                    "${index + 1}. ${data.question}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(start = 15.dp).animateContentSize()) {
                    data.answers.forEachIndexed { index, it ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            when (data) {
                                is Question.SeveralAnswer -> {
                                    Checkbox(
                                        checked = data.trueAnswers[index],
                                        onCheckedChange = {
                                            onChangeAnswer(index)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkmarkColor = LazyTheme.colors.questionBackground,
                                            checkedColor = LazyTheme.colors.onQuestionBackground,
                                            uncheckedColor = LazyTheme.colors.onQuestionBackground
                                        )
                                    )
                                }

                                is Question.SingleAnswer -> {
                                    RadioButton(
                                        selected = data.trueAnswerIndex == index,
                                        onClick = {
                                            onChangeAnswer(index)
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = LazyTheme.colors.onQuestionBackground,
                                            unselectedColor = LazyTheme.colors.onQuestionBackground
                                        )
                                    )
                                }
                            }

                            Text("${index + 1}. $it", fontSize = 16.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
