package promisor.promisor.domain.calender.exception;

import promisor.promisor.global.error.ErrorCode;
import promisor.promisor.global.error.exception.EntityNotFoundException;

public class DateEmptyException extends EntityNotFoundException {
    public DateEmptyException() {super(ErrorCode.DATE_EMPTY);}
}
