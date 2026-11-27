package com.foodrescue.fooddonation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
        @NotBlank(message = "name est obligatoire") String name,
        @NotBlank(message = "city est obligatoire") String city,
        @NotBlank(message = "contactEmail est obligatoire")
        @Email(message = "contactEmail doit etre une adresse valide") String contactEmail) {
}
