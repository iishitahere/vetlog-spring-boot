package com.jos.dem.vetlog.command

import javax.validation.constraints.Size
import javax.validation.constraints.NotNull
import org.hibernate.validator.constraints.Email

class RecoveryPasswordCommand implements Command {

  @NotNull
  @Size(min=6, max=200)
  @Email
  String email

}
