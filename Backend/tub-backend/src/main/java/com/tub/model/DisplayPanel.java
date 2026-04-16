package com.tub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "painel_pmd")
public class DisplayPanel {

    @Id
    private String panelId;
    
    private String location;
    private String message;
    private String status;
    private LocalDateTime timestamp;

    // Construtor vazio obrigatório para o JPA
    public DisplayPanel() {}

    // Construtor completo para facilitar a criação de objetos
    public DisplayPanel(String panelId, String location, String message, String status, LocalDateTime timestamp) {
        this.panelId = panelId;
        this.location = location;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters e Setters
    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}