package es.miapp.ad.ej4amigosagendafirebase.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    private long id;
    private String name, phNumber;
    private long dateOfBirth;
}
