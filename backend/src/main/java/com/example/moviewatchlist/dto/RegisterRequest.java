package com.example.moviewatchlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    @NotBlank(message = "Username wajib diisi")
    @Size(min = 3, message = "Username minimal 3 karakter")
    private String username;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;
}
