package br.com.intelibank.messagehub.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificationEmailEvent {
    private String type;
    private String email;
    private String key;
}
