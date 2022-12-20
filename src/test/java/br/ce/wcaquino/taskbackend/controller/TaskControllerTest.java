package br.ce.wcaquino.taskbackend.controller;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;

public class TaskControllerTest {

    @Mock
    private TaskRepo taskRepo;

    @InjectMocks
    private TaskController controller;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void shouldNotSaveTaskWithoutDescription(){
        Task task = new Task();
        task.setDueDate(LocalDate.now());
        try {
            controller.save(task);
            Assert.fail("Shouldn't get here!!!");
        } catch (ValidationException e) {
            Assert.assertEquals("Fill the task description", e.getMessage());
        }
    }

    @Test
    public void shouldNotSaveTaskWithoutDate(){
        Task task = new Task();
        task.setTask("Task test");
        try {
            controller.save(task);
            Assert.fail("Shouldn't get here!!!");
        } catch (ValidationException e) {
            Assert.assertEquals("Fill the due date", e.getMessage());
        }
    }

    @Test
    public void shouldNotSaveTaskWithPastDate(){
        Task task = new Task();
        task.setTask("Task test");
        task.setDueDate(LocalDate.of(2001, 1, 1));
        try {
            controller.save(task);
            Assert.fail("Shouldn't get here!!!");
        } catch (ValidationException e) {
            Assert.assertEquals("Due date must not be in past", e.getMessage());
        }
    }

    @Test
    public void shouldSaveTaskWhenSendingCorrectDatas() throws ValidationException{
        Task task = new Task();
        task.setTask("Task test");
        task.setDueDate(LocalDate.now());
        controller.save(task);
        Mockito.verify(taskRepo).save(task);        
    }
}
