package org.sumanta.rest.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class ByteArrayStreamingOutput implements StreamingOutput {

    private ByteArrayInputStream byteArrayInputStream;

    public ByteArrayStreamingOutput(ByteArrayInputStream byteArrayInputStream) {
        this.byteArrayInputStream = byteArrayInputStream;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        ByteArrayInputStream input = byteArrayInputStream;
        try {
            int bytes;
            while ((bytes = input.read()) != -1) {
                output.write(bytes);
            }
        } catch (Exception e) {
            throw new WebApplicationException(e);
        } finally {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
        }
    }

}