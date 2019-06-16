package hibernate.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;
import hibernate.demo.entity.Instructor;
import hibernate.demo.entity.InstructorDetail;
import org.hibernate.Session;

import javax.servlet.annotation.WebServlet;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** http://localhost:8080/One-to-One-bi-1.0-SNAPSHOT/InstructorUIServlet
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        GridLayout layout = new GridLayout(4,4);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {


            List<InstructorDetail> instructorsDetails = session.createQuery("from InstructorDetail", InstructorDetail.class).list();

            session.beginTransaction();

            InstructorDetail unwantedInstructorDeatails =
                    instructorsDetails
                            .stream()
                            .max(Comparator.comparingInt(InstructorDetail::getId))
                            .orElseThrow(NoSuchElementException::new);

            session.delete(unwantedInstructorDeatails);
            session.getTransaction().commit();

			Instructor tempInstructor =
					new Instructor("Chad", "Chady", "chady@freemail.com");

			InstructorDetail tempInstructorDetail =
					new InstructorDetail(
							"someHandsomeGuy",
							"surfing");

            tempInstructor.setInstructorDetail(tempInstructorDetail);

            System.out.println(tempInstructor);

            session.beginTransaction();
            session.save(tempInstructor);
            session.getTransaction().commit();



            List<Instructor> instructors = session.createQuery("from Instructor", Instructor.class).list();



            Grid<Instructor> gridInstructors = new Grid<>(Instructor.class);
            gridInstructors.setItems(instructors);
            layout.addComponent(gridInstructors,0,0,3,2);

            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        setContent(layout);

    }

    @WebServlet(urlPatterns = "/*", name = "InstructorUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }



}
