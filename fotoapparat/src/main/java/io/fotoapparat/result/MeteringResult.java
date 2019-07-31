package io.fotoapparat.result;

/**
 * The result of an attempt to lock the focus.
 */
public class MeteringResult {

    /**
     * {@code True} if the camera succeeded to measure the exposition
     */
    public final boolean succeeded;

    public MeteringResult(boolean succeeded) {
        this.succeeded = succeeded;
    }

    /**
     * Creates a new instance which has neither succeeded nor needs exposure measurement.
     *
     * @return A new, invalid {@link FocusResult}
     */
    public static MeteringResult failure() {
        return new MeteringResult(false);
    }

    /**
     * Creates a new instance which has succeeded but doesn't need exposure measurement.
     *
     * @return A new {@link FocusResult}
     */
    public static MeteringResult success() {
        return new MeteringResult(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeteringResult that = (MeteringResult) o;

        return succeeded == that.succeeded;
    }

    @Override
    public int hashCode() {
        int result = (succeeded ? 1 : 0);
        result = 31 * result;
        return result;
    }
}
