package se.jbnu.yangdoplatform.model

class FAQModel {
    var title: String? = null
    var content: String? = null
    var category: String? = null

    internal constructor() {}
    constructor(title: String?, content: String?, category: String?) {
        this.title = title
        this.content = content
        this.category = category
    }
}