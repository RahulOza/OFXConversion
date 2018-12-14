package pdftoofx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

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

        //uploadForm is the default page !!
        return "uploadForm";

        //return "pdftoofx/tempIndex.html";
        //return "response.html";

        //return "form.html";
        //return "index.html#ConvertComplete";
    }

   @RequestMapping(value = "/formsubmit", method = RequestMethod.POST)
    public String mysubmitContentPost(ModelMap modelMap){

        if(modelMap.containsValue("PDFDir")){


   }
        //return "index.html#ConvertComplete";

       // return "response";

       return "index.html";

    }
   @RequestMapping(value = "/formsubmit", method = RequestMethod.GET)
    public String mysubmitContentGet(){

        //return "index.html#ConvertComplete";

        return "response.html";
        //return "index.html";

    }

    /*@RequestMapping(value = "/error")
    public String errorResponse() {
        //return "index.html";
        //return "pdftoofx/tempIndex.html";
        return "Error.html";
        //return "index.html#ConvertComplete";
    }*/
   /* @PostMapping("/form")
    public String formPost() {

        return "index.html";
    }*/
}