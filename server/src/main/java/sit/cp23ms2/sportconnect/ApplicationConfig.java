package sit.cp23ms2.sportconnect;

import org.modelmapper.PropertyMap;
import sit.cp23ms2.sportconnect.dtos.activity.ActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity_participants.ActivityParticipantsDto;
import sit.cp23ms2.sportconnect.dtos.request.RequestDto;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.Request;
import sit.cp23ms2.sportconnect.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("POST", "GET",  "PUT", "OPTIONS", "DELETE", "PATCH");
        registry.addMapping("/**").allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept");
    }
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Request, RequestDto>() {
            protected void configure() {
                map().setUserId(source.getUser().getUserId());
                map().setActivityId(source.getActivity().getActivityId());
                // map other properties
            }
        });
        modelMapper.addMappings(new PropertyMap<ActivityParticipant, ActivityParticipantsDto>() {
            protected void configure() {
                map().setUserId(source.getUser().getUserId());
                map().setActivityId(source.getActivity().getActivityId());
            }
        });
        modelMapper.addMappings(new PropertyMap<Activity, ActivityDto>() {
            protected void configure() {
                map().setHostUserId(source.getHostUser().getUserId());
            }
        });
        return modelMapper;
    }
    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }
}
