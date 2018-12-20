package pdftoofx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

/*

If you use thymeleaf ...request is serviced from templates. (as content is dynamic)


TODO change this section once website is working

Currently we are just working with uploadForm.html and trying to get back pages from static folder !!

*/


@Controller
public class HomeController {

   /*private final StorageService storageService;

    @Autowired
    public void FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }*/

    @RequestMapping(value = "/")
    public String index() {

       /* public String listUploadedFiles(Model model) throws IOException {

            model.addAttribute("files", storageService.loadAll().map(
                    path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                            "serveFile", path.getFileName().toString()).build().toString())
                    .collect(Collectors.toList()));

            return "uploadForm";
        }*/


       // return "index";
        return "uploadForm";
    }

 /*  @RequestMapping(value = "/formsubmit", method = RequestMethod.POST)
   // public String mysubmitContentPost(ModelMap modelMap){
   public String mysubmitContentPost(){
        //if(modelMap.containsValue("PDFDir")){
        //}


        //return "index.html#ConvertComplete";

       // return "response";


    }*/


    @RequestMapping(value = "/formsubmit" )
    public String mysubmitContentPost(){




        return "response";


    }

    /*
@RequestMapping(value = "/error")
public String errorResponse() {

return "Error.html";

}
*/

}