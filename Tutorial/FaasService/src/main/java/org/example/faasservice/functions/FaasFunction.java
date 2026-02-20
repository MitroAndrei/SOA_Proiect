package org.example.faasservice.functions;

import org.example.faasservice.model.FunctionRequest;
import org.example.faasservice.model.FunctionResponse;

public interface FaasFunction {
    String getName();
    FunctionResponse execute(FunctionRequest request);
}
