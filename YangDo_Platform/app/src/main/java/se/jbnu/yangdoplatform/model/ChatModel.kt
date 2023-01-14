package se.jbnu.yangdoplatform.model

class ChatModel {
    @JvmField
    var users: HashMap<String, Boolean> = HashMap() //채팅방 유저
    @JvmField
    var comments: Map<String, Comment> = HashMap() //채팅 메시지

    class Comment {
        @JvmField
        var uid: String? = null
        @JvmField
        var message: String? = null
        @JvmField
        var timestamp: Any? = null
    }
}