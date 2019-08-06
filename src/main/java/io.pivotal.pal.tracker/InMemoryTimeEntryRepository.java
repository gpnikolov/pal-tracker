package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository{
    List<TimeEntry> repo = new ArrayList<>();
    private long counter = 0;
    public InMemoryTimeEntryRepository() {
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(++counter);
        repo.add(timeEntry);
        return timeEntry;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        for(TimeEntry entry: repo){
            if(entry.getId() == timeEntryId){
                return entry;
            }
        }

        return null;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry updateThisOne = find(id);
        if(updateThisOne != null){
            repo.remove(updateThisOne);
            timeEntry.setId(id);
            repo.add(timeEntry);
            return timeEntry;
        }
        return null;
    }

    @Override
    public List<TimeEntry> list() {
        return repo;
    }

    @Override
    public void delete(long id) {
        TimeEntry updateThisOne = find(id);
        if(updateThisOne != null) {
            repo.remove(updateThisOne);
        }
    }
}
