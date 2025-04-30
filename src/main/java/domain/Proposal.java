package domain;

import java.time.LocalDate;
import java.util.List;

public class Proposal {
    private String id;
    private String title;
    private String description;
    private String autorId;
    private LocalDate limitDate;
    private List<Vote> votes;
    private List<Comment> comments;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getAutorId() {
        return autorId;
    }
    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }
    public LocalDate getLimitDate() {
        return limitDate;
    }
    public void setLimitDate(LocalDate limitDate) {
        this.limitDate = limitDate;
    }
    public List<Vote> getVotes() {
        return votes;
    }
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
