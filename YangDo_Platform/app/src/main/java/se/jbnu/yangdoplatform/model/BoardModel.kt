package se.jbnu.yangdoplatform.model

class BoardModel {
    var title: String? = null
    var content: String? = null

    internal constructor() {}
    constructor(title: String?, content: String?) {
        this.title = title
        this.content = content
    }
}