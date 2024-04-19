package tel.kontra.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    
    private String id;
    private String name;
    private int age;
    private String city;
}
