package pdftoofx;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pdftoofx.storage.StorageFileNotFoundException;
import pdftoofx.storage.StorageService;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        /*model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList())); */

        //return "uploadForm";
        return "index";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/formsubmit")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam("statement") String statement) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "Successfully uploaded " + file.getOriginalFilename() + "!");

        // Now that file has been uploaded, convert it
        if(statement.equals("TSB")) {
            try {
                PdfToOfx.convertFileTSB(storageService.load(file.getOriginalFilename()).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(statement.equals("Select")) {
            try {
                PdfToOfx.convertFileTSB(storageService.load(file.getOriginalFilename()).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }if(statement.equals("Amazon")) {
            try {
                PdfToOfx.convertFileTSB(storageService.load(file.getOriginalFilename()).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "We are not yet ready for " + statement + "!");
        }
        // return "redirect:/";
        return "redirect:/response";
    }
    @GetMapping("/response")
    public String respondFileUpload(Model model) throws IOException {


        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));


        return "response";
    }

    @GetMapping("/clearform")
    public String clearAndBack() throws IOException{
        storageService.deleteAll();
        storageService.init();
        return "index";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
