package models

import kotlinx.serialization.Serializable

@Serializable
data class Test(
    val info: TestInfo,
    val questions: List<Question>
) {
    companion object {
        val Default = Test(TestInfo.Default, emptyList())
    }
}

@Serializable
data class TestInfo(val title: String) {
    companion object {
        val Default = TestInfo("")
    }
}