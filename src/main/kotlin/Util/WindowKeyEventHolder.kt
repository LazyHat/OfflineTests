package Util

//import androidx.compose.ui.input.key.Key
//import androidx.compose.ui.input.key.KeyEventType

//data class KeyParameters(
//    val key: Key,
//    val isCtrlPressed: Boolean = false,
//    val isAltPressed: Boolean = false,
//    val inShiftPressed: Boolean = false,
//    val isMetaPressed: Boolean = false,
//    val keyEventType: KeyEventType = KeyEventType.KeyDown
//)

//class WindowKeyEventHolder {
//    private val _eventSubscribers = MutableStateFlow(mapOf<KeyParameters, () -> Unit>().toImmutableMap())
//    private val eventListenerSetted = MutableStateFlow(false)
//
//    /*
//    val for setting window key event callback cannot be used secondly
//    */
//    val onKeyEvent: (KeyEvent) -> Boolean = {
//        _eventSubscribers.value.get(
//            KeyParameters(
//                it.key,
//                it.isCtrlPressed,
//                it.isAltPressed,
//                it.isShiftPressed,
//                it.isMetaPressed,
//                it.type
//            )
//        )?.let {
//            it.invoke()
//            true
//        } ?: false
//    }
//        get() {
//            check(!eventListenerSetted.value) { "ALREADY SETTED KEYEVENT LISTENER" }
//            eventListenerSetted.value = true
//            return field
//        }
//
//    fun subscribeKeyEvent(keyParameters: KeyParameters, callback: () -> Unit) {
//        check(!_eventSubscribers.value.contains(keyParameters)) { "ALREADY SUBSCRIBED FOR KEY EVENT WITH KEY PARAMS: $keyParameters" }
//        _eventSubscribers.update { (it + Pair(keyParameters, callback)).toImmutableMap() }
//    }
//
//    fun isSubscribed(keyParameters: KeyParameters): Boolean = _eventSubscribers.value.contains(keyParameters)
//}
