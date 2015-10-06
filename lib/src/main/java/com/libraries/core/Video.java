package com.libraries.core;

import java.io.Serializable;

public class Video implements Serializable
{
    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    private String iso_639_1;

    public String getIso6391() { return this.iso_639_1; }

    public void setIso6391(String iso_639_1) { this.iso_639_1 = iso_639_1; }

    private String key;

    public String getKey() { return this.key; }

    public void setKey(String key) { this.key = key; }

    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String site;

    public String getSite() { return this.site; }

    public void setSite(String site) { this.site = site; }

    private int size;

    public int getSize() { return this.size; }

    public void setSize(int size) { this.size = size; }

    private String type;

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Movie)
        {
            sameSame = this.id == ((Video) object).id;
        }

        return sameSame;
    }
}
