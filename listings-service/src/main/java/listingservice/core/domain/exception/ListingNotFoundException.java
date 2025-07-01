package listingservice.core.domain.exception;

public class ListingNotFoundException extends RuntimeException{
    public ListingNotFoundException(String message) {
        super(message);
    }
}
