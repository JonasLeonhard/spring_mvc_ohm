package pojos

import javax.validation.constraints.*

data class InvitationForm(
        @field:NotNull(message = "Recipe Id has to be specified")
        @field:Positive(message = "Recipe Id has to be positive")
        val recipeId: Long?,

        @field:NotNull(message = "Invitation title has to be specified")
        @field:NotBlank(message = "Invitation title cannot be empty")
        val title: String?,

        @field:NotNull(message = "Invitation message has to be specified")
        @field:NotBlank(message = "Invitation message cannot be empty")
        val message: String?,

        @field:NotNull(message = "Invitation start time has to be specified")
        @field:PositiveOrZero(message = "Invitation start time has to be positive or zero")
        @field:Max(96, message = "Invitation start time has to be below 96 (24h * 1/4)")
        val gridRowStart: Int?,

        @field:NotNull(message = "Invitation end time has to be specified")
        @field:PositiveOrZero(message = "Invitation end time has to be positive or zero")
        @field:Max(96, message = "Invitation end time has to be below 96 (24h * 1/4)")
        val gridRowEnd: Int?,

        @field:NotNull(message = "Friends to be invited have to be specified")
        val friends: MutableList<String>?,

        @field:NotNull(message = "Invitation Date has to be specified")
        @Pattern(regexp = "(0[1-9]|[12][0-9]|[3][01])/(0[1-9]|1[012])/\\d{4}", message = "Date format must be: 'dd/mm/yyyy'")
        val date: String?
)