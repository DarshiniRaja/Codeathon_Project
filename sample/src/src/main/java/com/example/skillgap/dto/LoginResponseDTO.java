package com.example.skillgap.dto;

public class LoginResponseDTO {

    private String token;
    private UserDTO user;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public static class UserDTO {
        private Long id;
        private String name;
        private String email;
        private String role;
        private Long studentId;

        public UserDTO() {
        }

        public UserDTO(Long id, String name, String email, String role, Long studentId) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.studentId = studentId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }
    }
}
