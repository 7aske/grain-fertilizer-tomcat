package com._7aske.grain.fertilizer.web.server.tomcat.adapter;

import com._7aske.grain.exception.GrainRuntimeException;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;

/**
 * Adapts the {@link Part} to Grain {@link com._7aske.grain.web.http.multipart.Part}.
 */
public class PartAdapter implements Part, com._7aske.grain.web.http.multipart.Part {
    private final Part part;

    public PartAdapter(Part part) {
        this.part = Objects.requireNonNull(part);
    }

    @Override
    public String getFileName() {
        return getSubmittedFileName();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return part.getInputStream();
        } catch (IOException e) {
            throw new GrainRuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return part.getContentType();
    }

    @Override
    public String getName() {
        return part.getName();
    }

    @Override
    public String getSubmittedFileName() {
        return part.getSubmittedFileName();
    }

    @Override
    public long getSize() {
        return part.getSize();
    }

    @Override
    public void write(String fileName) throws IOException {
        part.write(fileName);
    }

    @Override
    public void delete() throws IOException {
        part.delete();
    }

    @Override
    public String getHeader(String name) {
        return part.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return part.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return part.getHeaderNames();
    }
}
