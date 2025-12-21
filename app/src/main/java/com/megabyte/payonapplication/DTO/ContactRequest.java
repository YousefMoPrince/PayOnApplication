package com.megabyte.payonapplication.DTO;

import java.util.List;

public class ContactRequest {
    private List<String> contactNumbers;

    public List<String> getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(List<String> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }
}
