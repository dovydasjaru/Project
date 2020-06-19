package mif.Utility;

import net.corda.core.flows.FlowException;
import net.corda.core.utilities.UntrustworthyData;

import java.security.PublicKey;

public class GenericValidator<T> implements UntrustworthyData.Validator<T, T> {
    @Override
    public T validate(T data) throws FlowException {
        return data;
    }
}