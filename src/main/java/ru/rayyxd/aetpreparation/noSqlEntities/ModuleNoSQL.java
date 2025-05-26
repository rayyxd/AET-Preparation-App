package ru.rayyxd.aetpreparation.noSqlEntities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;


@Document(collection = "modules")
public class ModuleNoSQL {

    @Id
    private ObjectId id; 
    
    @Indexed(unique = true)
    private int moduleId;
    
    private List<ContentItem> content; 
    
    // Геттеры и сеттеры
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
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class ContentItem {
        protected String type;
        
        public String getType() { return type; }
       
        public void setType(String type) { this.type = type; }
        
        // picture 
        private String link;

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        
        //quiz
        
        private String question;
        private String correct_answer;
        private List<String> additional_answer;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getCorrect_answer() { return correct_answer; }
        public void setCorrect_answer(String correct_answer) { this.correct_answer = correct_answer; }

        public List<String> getAdditional_answer() { return additional_answer; }
        public void setAdditional_answer(List<String> additional_answer) { this.additional_answer = additional_answer; }
        
        //text
        
        private String text;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        //video
    }
    
}