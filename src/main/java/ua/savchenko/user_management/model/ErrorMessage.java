package ua.savchenko.user_management.model;

import org.springframework.http.HttpStatusCode;

public record ErrorMessage(String errorMessage, HttpStatusCode httpStatus, int statusCode) {
}
