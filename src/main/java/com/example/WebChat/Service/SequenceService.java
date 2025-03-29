package com.example.WebChat.Service;

import com.example.WebChat.Entity.Sequence;
import com.example.WebChat.Repo.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

    @Autowired
    private SequenceRepository sequenceRepository;

    public int getNextSequence(String collectionName) {
        Sequence sequence = sequenceRepository.findById(collectionName)
                .orElse(new Sequence(collectionName, 0));
        int seq = sequence.getSeq() + 1;
        sequence.setSeq(seq);
        sequenceRepository.save(sequence);
        return seq;
    }

    public String generateRollNoForCourse(String courseName) {
        // Example: "B-01", "M-02", ...
        String prefix;
        switch (courseName.toLowerCase()) {
            case "b.sc":
            case "bsc":
                prefix = "B";
                break;
            case "m.tech":
            case "mtech":
                prefix = "M";
                break;
            case "b.tech":
            case "btech":
                prefix = "T";
                break;
            default:
                prefix = "C";
                break;
        }
        String sequenceKey = "seq_" + prefix;
        int nextSeq = getNextSequence(sequenceKey);
        return prefix + "-" + "%02d".formatted(nextSeq);
    }
}
