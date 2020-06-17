package io.aftersound.weave.common;

public final class Result extends Container<Result> {

    public static final Key<Results> SUB_RESULTS = Key.of("SUB_RESULTS");
    public static final Key<Results> SUB_RESULTS_SUCCEEDED = Key.of("SUB_RESULTS_SUCCEEDED");
    public static final Key<Results> SUB_RESULTS_FAILED = Key.of("SUB_RESULTS_FAILED");

    public static final Key<ReturnInfo> RETURN_INFO = Key.of("ReturnInfo");
    public static final Key<ReturnInfos> RETURN_INFOS = Key.of("ReturnInfos");

    private static abstract class Indicator {

        private final String message;

        Indicator(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    public static class Success extends Indicator {

        private final String message;

        public Success(String message) {
            super(message);
            this.message = message;
        }

    }

    public static class Failure extends Indicator {

        private final Throwable cause;

        public Failure(Throwable cause) {
            super(cause.getMessage());
            this.cause = cause;
        }

        public Failure(String message, Throwable cause) {
            super(message);
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }
    }

    private static class Void extends Indicator {

        Void(String message) {
            super(message);
        }
    }

    private final Indicator indicator;
    private String hint;

    // Only valid in case of composite result
    private boolean isComposite;

    private Result(Indicator indicator) {
        this.indicator = indicator;
    }

    public static Result composite(int size) {
        Result result = new Result(new Void("void"));
        result.isComposite = true;
        result.set(SUB_RESULTS, new Results(size));
        result.set(SUB_RESULTS_SUCCEEDED, new Results(size));
        result.set(SUB_RESULTS_FAILED, new Results(size));
        result.set(RETURN_INFOS, new ReturnInfos());
        return result;
    }

    public static Result success() {
        return new Result(new Success(null));
    }

    public static Result success(String message) {
        return new Result(new Success(message));
    }

    public static Result failure(String message) {
        return new Result(new Failure(message, null));
    }

    public static Result failure(Throwable cause) {
        return new Result(new Failure(cause.getMessage(), cause));
    }

    public static Result failure(String message, Throwable cause) {
        return new Result(new Failure(cause.getMessage(), cause));
    }

    public Result addSubResult(Result subResult) {
        if (isComposite) {
            get(SUB_RESULTS).add(subResult);
            if (subResult.isSuccess()) {
                get(SUB_RESULTS_SUCCEEDED).add(subResult);
                get(RETURN_INFOS).addSucceeded(subResult.get(RETURN_INFO));
            } else {
                get(SUB_RESULTS_FAILED).add(subResult);
                get(RETURN_INFOS).addFailed(subResult.get(RETURN_INFO));
            }
        }
        return this;
    }

    public boolean isSuccess() {
        if (isComposite) {
            return get(SUB_RESULTS).size() == get(SUB_RESULTS_SUCCEEDED).size();
        } else {
            return indicator instanceof Success;
        }
    }

    public boolean isFailure() {
        if (isComposite) {
            return get(SUB_RESULTS).size() > 0 && get(SUB_RESULTS_SUCCEEDED).size() == 0;
        } else {
            return indicator instanceof Failure;
        }
    }

    public boolean isPartialFailure() {
        return isComposite && get(SUB_RESULTS_SUCCEEDED).size() > 0 && get(SUB_RESULTS_FAILED).size() > 0;
    }

    public String getMessage() {
        return indicator.getMessage();
    }

    public String getFailureReason() {
        return indicator.getMessage();
    }

    public String getHint() {
        return hint;
    }

    public Result setHint(String hint) {
        this.hint = hint;
        return this;
    }

}
