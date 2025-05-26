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
    
    @Indexed(unique = true)
    private int moduleId;
    
    private List<Question> questions; 
    
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
    
    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setContent(List<Question> questions) {
        this.questions = questions;
    }
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Question {
        private String question;
        private String correctAnswer;
        private List<String> additionalQuestions;
        
        public String getQuestion() {
			return question;
		}
        
        public void setQuestion(String question) {
			this.question = question;
		}
        
        public String getCorrectAnswer() {
			return correctAnswer;
		}
        
        public void setCorrectAnswer(String correctAnswer) {
			this.correctAnswer = correctAnswer;
		}
        
        public List<String> getAdditionalQuestions() {
			return additionalQuestions;
		}
        
        public void setAdditionalQuestions(List<String> additionalQuestions) {
			this.additionalQuestions = additionalQuestions;
		}
    }
}
