package de.holisticon.http2clearcase.rest;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Greeting {

  private long id;
  private String content;

}
