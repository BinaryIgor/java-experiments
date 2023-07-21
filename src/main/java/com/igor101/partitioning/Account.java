package com.igor101.partitioning;

import java.util.UUID;

public record Account(UUID id, String name, int countryCode, String description) {

}
