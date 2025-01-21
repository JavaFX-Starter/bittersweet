package com.icuxika.bittersweet.demo.controller

import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.dsl.onAction
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.scene.text.Font
import javafx.scene.text.TextFlow
import javafx.util.Callback
import java.net.URL
import java.util.*

@AppFXML(fxml = "fxml/chat-view.fxml")
class ChatViewController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    private val messageObservableList: ObservableList<Message> = FXCollections.observableArrayList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        container.top = Button("测试").apply {
            onAction {
                messageObservableList.filter { it.type == 2 }[0].imageUrl =
                    "https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png"
                (container.center as ListView<*>).refresh()
            }
        }
        container.center = ListView<Message>().apply {
            styleClass.add("message-list-view")
            items = messageObservableList
            cellFactory = Callback<ListView<Message>, ListCell<Message>> {
                object : ListCell<Message>() {
                    val leftMsgDecorateTextFlow by lazy {
                        createTextFlow(
                            "M-0,0c0,565.161 458.839,1024 1024,1024l-0,-716.8c-408.482,0 -785.067,-137.652 -1024,-307.2Z"
                        )
                    }
                    val rightMsgDecorateTextFlow by lazy {
                        createTextFlow(
                            "M0,307.2l0,716.8c565.161,0 1024,-458.839 1024,-1024c-238.933,169.548 -615.518,307.2 -1024,307.2Z"
                        )
                    }

                    val textProperty = SimpleStringProperty()
                    val leftTextNode by lazy {
                        val text = createText()
                        AnchorPane().apply {
                            AnchorPane.setLeftAnchor(text, 24.0)
                            AnchorPane.setTopAnchor(text, 0.0)
                            AnchorPane.setLeftAnchor(leftMsgDecorateTextFlow, 4.0)
                            AnchorPane.setTopAnchor(leftMsgDecorateTextFlow, 2.0)
                            children.addAll(text, leftMsgDecorateTextFlow)
                        }
                    }
                    val rightTextNode by lazy {
                        val text = createText()
                        AnchorPane().apply {
                            AnchorPane.setRightAnchor(text, 24.0)
                            AnchorPane.setTopAnchor(text, 0.0)
                            AnchorPane.setRightAnchor(rightMsgDecorateTextFlow, 4.0)
                            AnchorPane.setTopAnchor(rightMsgDecorateTextFlow, 2.0)
                            children.addAll(text, rightMsgDecorateTextFlow)
                        }
                    }

                    fun createText(): TextFlow = TextFlow().apply {
                        padding = Insets(8.0)
                        background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii(16.0), Insets.EMPTY))
                        children.add(Label().apply {
                            textProperty().bind(textProperty)
                            textFill = Color.WHITE
                            font = Font.font(16.0)
                            maxWidth = 240.0
                            isWrapText = true
                        })
                    }

                    val imageProperty = SimpleObjectProperty<Image>()
                    val fitWidthProperty = SimpleDoubleProperty()
                    val leftImageNode by lazy {
                        val image = createImage()
                        AnchorPane().apply {
                            AnchorPane.setLeftAnchor(image, 24.0)
                            AnchorPane.setTopAnchor(image, 0.0)
                            AnchorPane.setLeftAnchor(leftMsgDecorateTextFlow, 4.0)
                            AnchorPane.setTopAnchor(leftMsgDecorateTextFlow, 2.0)
                            children.addAll(image, leftMsgDecorateTextFlow)
                        }
                    }
                    val rightImageNode by lazy {
                        val image = createImage()
                        AnchorPane().apply {
                            AnchorPane.setRightAnchor(image, 24.0)
                            AnchorPane.setTopAnchor(image, 0.0)
                            AnchorPane.setRightAnchor(rightMsgDecorateTextFlow, 4.0)
                            AnchorPane.setTopAnchor(rightMsgDecorateTextFlow, 2.0)
                            children.addAll(image, rightMsgDecorateTextFlow)
                        }
                    }

                    fun createImage(): TextFlow = TextFlow().apply {
                        padding = Insets(8.0)
                        background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii(16.0), Insets.EMPTY))
                        val imageView = ImageView().apply {
                            imageProperty().bind(imageProperty)
                            fitWidthProperty().bind(fitWidthProperty)
                            isPreserveRatio = true
                            isCache = true
                        }
                        children.add(imageView)
                    }

                    override fun updateItem(item: Message?, empty: Boolean) {
                        super.updateItem(item, empty)

                        if (empty || item == null) {
                            text = null
                            graphic = null
                        } else {
                            text = null
                            graphic = when (item.type) {
                                1 -> {
                                    textProperty.bind(item.msgProperty())
                                    if (item.left) {
                                        leftTextNode
                                    } else {
                                        rightTextNode
                                    }
                                }

                                2 -> {
                                    if (imageProperty.get() == null || imageProperty.get().url != item.imageUrl) {
                                        imageProperty.set(Image(item.imageUrl, true).apply {
                                            progressProperty().addListener { observable, oldValue, newValue ->
                                                if (newValue == 1.0) {
                                                    println(imageProperty.get().width)
                                                    if (imageProperty.get().width > 240.0) {
                                                        fitWidthProperty.set(240.0)
                                                    }
                                                }
                                            }
                                        })
                                    }
                                    if (item.left) {
                                        leftImageNode
                                    } else {
                                        rightImageNode
                                    }
                                }

                                else -> throw IllegalArgumentException()
                            }
                        }
                    }
                }
            }
        }

        messageObservableList.addAll(
            Message().apply {
                left = true
                setMsg("你要好好长大，不要输给风，不要输给雨，不要输给冬雪，不要输给炎夏。少年人，你在孩童时应当快乐，使你的心欢畅，行你所愿行的，见你所愿见的，然而也应当记住黑暗的时日。但愿新的梦想永远不被无留陀侵蚀，但愿旧的故事与无留陀一同被忘却，但愿绿色的原野，山丘永远不会变得枯黄，但愿溪水永远清澈，但愿鲜花永远盛开。挚友将再次同行于茂密的森林中，一切美好的事物终将归来，一切痛苦的记忆也会远去，就像溪水净化自己，枯树绽出新芽。最终，森林会记住一切。")
            },
            Message().apply {
                left = false
                setMsg("如此绚丽的花朵，不该在绽放之前就枯萎。我会赠予你璀璨的祝福，而你的灵魂，也将绽放更耀眼的光辉。亲爱的山雀，请将我的箭，我的花，与我的爱，带给那孑然独行的旅人。愿你前行的道路有群星闪耀。愿你留下的足迹有百花绽放。你即是上帝的馈赠，世界因你而瑰丽。")
            },
            Message().apply {
                left = true
                type = 2
            },
            Message().apply {
                left = true
                type = 2
                imageUrl = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"
            },
            Message().apply {
                left = true
                setMsg("你要好好长大，不要输给风，不要输给雨，不要输给冬雪，不要输给炎夏。少年人，你在孩童时应当快乐，使你的心欢畅，行你所愿行的，见你所愿见的，然而也应当记住黑暗的时日。但愿新的梦想永远不被无留陀侵蚀，但愿旧的故事与无留陀一同被忘却，但愿绿色的原野，山丘永远不会变得枯黄，但愿溪水永远清澈，但愿鲜花永远盛开。挚友将再次同行于茂密的森林中，一切美好的事物终将归来，一切痛苦的记忆也会远去，就像溪水净化自己，枯树绽出新芽。最终，森林会记住一切。")
            },
            Message().apply {
                left = false
                setMsg("如此绚丽的花朵，不该在绽放之前就枯萎。我会赠予你璀璨的祝福，而你的灵魂，也将绽放更耀眼的光辉。亲爱的山雀，请将我的箭，我的花，与我的爱，带给那孑然独行的旅人。愿你前行的道路有群星闪耀。愿你留下的足迹有百花绽放。你即是上帝的馈赠，世界因你而瑰丽。")
            },
            Message().apply {
                left = true
                type = 2
                imageUrl = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"
            },
            Message().apply {
                left = true
                setMsg("你要好好长大，不要输给风，不要输给雨，不要输给冬雪，不要输给炎夏。少年人，你在孩童时应当快乐，使你的心欢畅，行你所愿行的，见你所愿见的，然而也应当记住黑暗的时日。但愿新的梦想永远不被无留陀侵蚀，但愿旧的故事与无留陀一同被忘却，但愿绿色的原野，山丘永远不会变得枯黄，但愿溪水永远清澈，但愿鲜花永远盛开。挚友将再次同行于茂密的森林中，一切美好的事物终将归来，一切痛苦的记忆也会远去，就像溪水净化自己，枯树绽出新芽。最终，森林会记住一切。")
            },
            Message().apply {
                left = true
                setMsg("你要好好长大，不要输给风，不要输给雨，不要输给冬雪，不要输给炎夏。少年人，你在孩童时应当快乐，使你的心欢畅，行你所愿行的，见你所愿见的，然而也应当记住黑暗的时日。但愿新的梦想永远不被无留陀侵蚀，但愿旧的故事与无留陀一同被忘却，但愿绿色的原野，山丘永远不会变得枯黄，但愿溪水永远清澈，但愿鲜花永远盛开。挚友将再次同行于茂密的森林中，一切美好的事物终将归来，一切痛苦的记忆也会远去，就像溪水净化自己，枯树绽出新芽。最终，森林会记住一切。")
            },
            Message().apply {
                left = true
                setMsg("你要好好长大，不要输给风，不要输给雨，不要输给冬雪，不要输给炎夏。少年人，你在孩童时应当快乐，使你的心欢畅，行你所愿行的，见你所愿见的，然而也应当记住黑暗的时日。但愿新的梦想永远不被无留陀侵蚀，但愿旧的故事与无留陀一同被忘却，但愿绿色的原野，山丘永远不会变得枯黄，但愿溪水永远清澈，但愿鲜花永远盛开。挚友将再次同行于茂密的森林中，一切美好的事物终将归来，一切痛苦的记忆也会远去，就像溪水净化自己，枯树绽出新芽。最终，森林会记住一切。")
            },
            Message().apply {
                left = false
                setMsg("如此绚丽的花朵，不该在绽放之前就枯萎。我会赠予你璀璨的祝福，而你的灵魂，也将绽放更耀眼的光辉。亲爱的山雀，请将我的箭，我的花，与我的爱，带给那孑然独行的旅人。愿你前行的道路有群星闪耀。愿你留下的足迹有百花绽放。你即是上帝的馈赠，世界因你而瑰丽。")
            },
        )
    }

    private fun createTextFlow(svgContent: String, size: Double = 24.0): TextFlow = TextFlow().apply {
        shape = SVGPath().apply {
            content = svgContent
        }
        background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY))
        minWidth = size
        minHeight = size
        maxWidth = size
        maxHeight = size
    }

    class Message {
        var left: Boolean = true

        var type: Int = 1

        private val msg = SimpleStringProperty()
        fun msgProperty() = msg
        fun setMsg(value: String) {
            msg.set(value)
        }

        fun getMsg() = msg.get()

        var imageUrl = "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/shuijiao.jpg"
    }
}