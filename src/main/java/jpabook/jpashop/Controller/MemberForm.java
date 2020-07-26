package jpabook.jpashop.Controller;

import lombok.Getter;
import javax.validation.constraints.NotEmpty;

import lombok.Setter;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "값은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
