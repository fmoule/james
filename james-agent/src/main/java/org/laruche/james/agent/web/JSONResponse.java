package org.laruche.james.agent.web;

import org.json.JSONObject;

import java.util.Objects;

import static org.laruche.james.agent.web.JSONResponse.ResponseStatus.KO;
import static org.laruche.james.agent.web.JSONResponse.ResponseStatus.OK;

/**
 * <p>
 * Classe utilisée pour les response des différentes API's exposées par
 * les agents web. <br />
 * </p>
 * @see RESTAgent
 */
public class JSONResponse<T> extends JSONObject {
    private static final String EMPTY_MESSAGE = "";
    private static final String RESPONSE_FIELD = "response";
    private static final String STATUS_FIELD = "status";
    private static final String ERROR_MESSAGE_FIELD = "errorMessage";

    private JSONResponse(final ResponseStatus status, final T response, final String errorMessage) {
        this.put(STATUS_FIELD, status);
        this.put(RESPONSE_FIELD, response);
        this.put(ERROR_MESSAGE_FIELD, errorMessage);
    }

    ///// Builders :

    public static <T> JSONResponse<T> createOKResponse(final T response) {
        return new JSONResponse<>(OK, response, EMPTY_MESSAGE);
    }

    public static <T> JSONResponse<T> createErrorResponse(final String errorMessage) {
        return new JSONResponse<>(KO, null, errorMessage);
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int hashCode = Objects.hashCode(this.getStatus());
        hashCode = (prime * hashCode) + Objects.hashCode(this.getResponse());
        hashCode = (prime * hashCode) + Objects.hashCode(this.getErrorMessage());
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        final JSONResponse<?> that = (JSONResponse<?>) obj;
        boolean areEquals = Objects.equals(this.getStatus(), that.getStatus());
        areEquals = areEquals && Objects.equals(this.getResponse(), that.getResponse());
        areEquals = areEquals && Objects.equals(this.getErrorMessage(), that.getErrorMessage());
        return areEquals;
    }

    ///// Getters & Setters :

    public T getResponse() {
        if (this.has(RESPONSE_FIELD)) {
            //noinspection unchecked
            return (T) this.get(RESPONSE_FIELD);
        } else {
            return null;
        }
    }

    private ResponseStatus getStatus() {
        return (ResponseStatus) this.get(STATUS_FIELD);
    }

    public String getErrorMessage() {
        String errorMessage;
        if (this.has(ERROR_MESSAGE_FIELD)) {
            errorMessage = (String) this.get(ERROR_MESSAGE_FIELD);
        } else {
            errorMessage = EMPTY_MESSAGE;
        }
        return errorMessage;
    }

    ///// Classes internes

    /**
     * <p>
     * Enumération utilisée pour le status de la réponse. <br />
     * </p>
     */
    public enum ResponseStatus {
        OK,
        KO
    }
}



