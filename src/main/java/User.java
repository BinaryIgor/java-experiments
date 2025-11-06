package com.igor101;

import java.time.Instant;
import java.util.UUID;

public record User(UUID id,
                   String name,
                   String email,
                   String status,
                   Instant createdAt) { }
