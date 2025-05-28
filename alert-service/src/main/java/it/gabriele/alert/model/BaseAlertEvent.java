package it.gabriele.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseAlertEvent {
    public String source;
    public LocalDateTime timestamp;
    public String message;
}