package ru.rayyxd.aetpreparation.noSqlEntities;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "tests")
public class FinalTestNoSql {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private int testId;
    
    private String title;

    private List<Question> content;

    // Геттеры и сеттеры
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }
    
    public String getTitle() {
		return title;
	}
    
    public void setTitle(String title) {
		this.title = title;
	}

    public List<Question> getContent() {
        return content;
    }

    public void setContent(List<Question> content) {
        this.content = content;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Question {
        private String question;
        private List<Option> options;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Option {
        private String text;
        private boolean isCorrect;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }
}
