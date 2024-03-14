package sit.cp23ms2.sportconnect;

import org.modelmapper.PropertyMap;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import sit.cp23ms2.sportconnect.dtos.activity.ActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity_participants.ActivityParticipantsDto;
import sit.cp23ms2.sportconnect.dtos.location.LocationDto;
import sit.cp23ms2.sportconnect.dtos.notification.NotificationDto;
import sit.cp23ms2.sportconnect.dtos.request.RequestDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.InterestDto;
import sit.cp23ms2.sportconnect.entities.*;
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;
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
        modelMapper.addMappings(new PropertyMap<Notification, NotificationDto>() {
            protected void configure() {
                map().setTargetId(source.getTargetId().getUserId());
            }
        });
        modelMapper.addMappings(new PropertyMap<UserInterest, InterestDto>() {
            protected void configure() {
                map().setUserId(source.getUser().getUserId());
                map().setEmail(source.getUser().getEmail());
                map().setCategoryName(source.getCategory().getName());
            }
        });
        return modelMapper;
    }
    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

    @Bean
    public AuthenticationUtil authenticationUtil() {
        return new AuthenticationUtil();
    }
}
