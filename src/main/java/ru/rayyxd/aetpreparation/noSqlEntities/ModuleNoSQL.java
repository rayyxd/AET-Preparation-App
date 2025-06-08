package ru.rayyxd.aetpreparation.noSqlEntities;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Сущность "Модуль" для MongoDB: хранит moduleId и список полиморфных блоков "content".
 * В одном файле находятся все подклассы ContentItem для удобства.
 */
@Document(collection = "modules")
public class ModuleNoSQL {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private int moduleId;

    private List<ContentItem> content;

    public ModuleNoSQL() {}

    public ModuleNoSQL(int moduleId, List<ContentItem> content) {
        this.moduleId = moduleId;
        this.content = content;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
    }

    /**
     * Абстрактный базовый класс для всех типов блоков "content".
     * Jackson читает поле "type" и десериализует каждый вложенный документ
     * в соответствующий подкласс (HeadingContent, ParagraphContent и т. д.).
     */
    @JsonTypeInfo(
    	    use = JsonTypeInfo.Id.NAME,
    	    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    	    property = "type",
    	    visible = true
    	)

    @JsonSubTypes({
        @JsonSubTypes.Type(value = HeadingContent.class,   name = "heading"),
        @JsonSubTypes.Type(value = ParagraphContent.class, name = "paragraph"),
        @JsonSubTypes.Type(value = ListContent.class,      name = "list"),
        @JsonSubTypes.Type(value = QuoteContent.class,     name = "quote"),
        @JsonSubTypes.Type(value = TableContent.class,     name = "table"),
        @JsonSubTypes.Type(value = QuizContent.class,      name = "quiz"),
        @JsonSubTypes.Type(value = PictureContent.class,   name = "picture"),
        @JsonSubTypes.Type(value = VideoContent.class,     name = "video")
    })
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static abstract class ContentItem {
        protected String type;

        public ContentItem() {}

        public ContentItem(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * Блок "заголовок": содержит уровень и текст.
     * JSON-структура:
     * {
     *   "type": "heading",
     *   "level": 1,
     *   "text": "Заголовок"
     * }
     */
    public static class HeadingContent extends ContentItem {
        private Integer level;
        private String text;

        public HeadingContent() {
            super("heading");
        }

        public HeadingContent(int level, String text) {
            super("heading");
            this.level = level;
            this.text = text;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * Блок "параграф": просто текст.
     * JSON-структура:
     * {
     *   "type": "paragraph",
     *   "text": "Какой-то текст..."
     * }
     */
    public static class ParagraphContent extends ContentItem {
        private String text;

        public ParagraphContent() {
            super("paragraph");
        }

        public ParagraphContent(String text) {
            super("paragraph");
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * Блок "список": имеет стиль (bullet/number) и список items.
     * JSON-структура:
     * {
     *   "type": "list",
     *   "style": "bullet",
     *   "items": ["пункт 1", "пункт 2", ...]
     * }
     */
    public static class ListContent extends ContentItem {
        private String style;
        private List<String> items;

        public ListContent() {
            super("list");
        }

        public ListContent(String style, List<String> items) {
            super("list");
            this.style = style;
            this.items = items;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    /**
     * Блок "цитата": просто текст в поле text.
     * JSON-структура:
     * {
     *   "type": "quote",
     *   "text": "«Некоторый цитатный текст»"
     * }
     */
    public static class QuoteContent extends ContentItem {
        private String text;

        public QuoteContent() {
            super("quote");
        }

        public QuoteContent(String text) {
            super("quote");
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * Блок "таблица": содержит headers и rows.
     * JSON-структура:
     * {
     *   "type": "table",
     *   "headers": ["Колонка 1", "Колонка 2"],
     *   "rows": [
     *     ["Ячейка 1.1", "Ячейка 1.2"],
     *     ["Ячейка 2.1", "Ячейка 2.2"]
     *   ]
     * }
     */
    public static class TableContent extends ContentItem {
        private List<String> headers;
        private List<List<String>> rows;

        public TableContent() {
            super("table");
        }

        public TableContent(List<String> headers, List<List<String>> rows) {
            super("table");
            this.headers = headers;
            this.rows = rows;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }

        public List<List<String>> getRows() {
            return rows;
        }

        public void setRows(List<List<String>> rows) {
            this.rows = rows;
        }
    }

    /**
     * Блок "quiz": содержит вопрос и список вариантов.
     * JSON-структура:
     * {
     *   "type": "quiz",
     *   "question": "Текст вопроса",
     *   "options": [
     *     {"text": "Вариант A", "isCorrect": true},
     *     {"text": "Вариант B", "isCorrect": false},
     *     ...
     *   ]
     * }
     */
    public static class QuizContent extends ContentItem {
        private String question;
        private List<QuizOption> options;

        public QuizContent() {
            super("quiz");
        }

        public QuizContent(String question, List<QuizOption> options) {
            super("quiz");
            this.question = question;
            this.options = options;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<QuizOption> getOptions() {
            return options;
        }

        public void setOptions(List<QuizOption> options) {
            this.options = options;
        }

        /**
         * Вложенный класс для одного варианта ответа в блоке QuizContent.
         */
        public static class QuizOption {
            private String text;

            @JsonProperty("isCorrect")
            private Boolean isCorrect;

            public QuizOption() {}

            public QuizOption(String text, Boolean isCorrect) {
                this.text = text;
                this.isCorrect = isCorrect;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            @JsonProperty("isCorrect")
            public Boolean getIsCorrect() {
                return isCorrect;
            }

            @JsonProperty("isCorrect")
            public void setIsCorrect(Boolean isCorrect) {
                this.isCorrect = isCorrect;
            }
        }
    }
    
    /**
     * Блок "picture": содержит ключ (key) объекта в S3 для отображения.
     * JSON-структура:
     * {
     *   "type": "picture",
     *   "link": "module-9-quiz-picture.png" // только ключ, не полный URL
     * }
     */
    public static class PictureContent extends ContentItem{
        private String link; // S3 key only
    	
    	public void setLink(String link) {
			this.link = link;
		}
    	
    	public String getLink() {
			return link;
		}
    	
    	public PictureContent() {
			// TODO Auto-generated constructor stub
		}
    	
    	public PictureContent(String link) {
    		this.link=link;
    	}
    }
    
    /**
     * Блок "video": содержит ключ (key) объекта в S3 для отображения.
     * JSON-структура:
     * {
     *   "type": "video",
     *   "link": "module-9-logic-2.mp4" // только ключ, не полный URL
     * }
     */
    public static class VideoContent extends ContentItem{
        private String link; // S3 key only
    	
    	public void setLink(String link) {
			this.link = link;
		}
    	
    	public String getLink() {
			return link;
		}
    	
    	public VideoContent() {
			// TODO Auto-generated constructor stub
		}
    	
    	public VideoContent(String link) {
    		this.link = link;
    	}
    }
}
