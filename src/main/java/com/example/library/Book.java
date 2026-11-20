package com.example.library;

import java.time.OffsetDateTime;

public record Book(
    long id,
    String title,
    String author,
    String lentTo,
    OffsetDateTime lentAt,
    OffsetDateTime createdAt) {}
