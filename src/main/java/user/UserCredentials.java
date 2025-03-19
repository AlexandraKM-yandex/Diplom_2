package user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserCredentials {

    private String email;
    private String password;
    private String name;

    public static UserCredentials credentialsFromUser(User user) {
        return new UserCredentials(user.getEmail(), user.getPassword(), user.getName());
    }
}
