import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';
import { Button, Form, Modal} from 'react-bootstrap';
import ProgressBar from 'react-bootstrap/ProgressBar';
import axios from 'axios';


const CreatePagePostModal = (props) => {
    const user = useUser();
    const {pageId} = useParams();
    const {postShow, emitHandlePostClose} = props;
    const formData = new FormData();
    
    const [file, setFile] = useState(null);
    const [post, setPost] = useState("");
    const [fileUploaded, setFileUploaded] = useState(0);
    const [isFileValid, setIsFileValid] = useState(false);

    const max_img_size = 8388608;
    const max_video_size = 2147483648;
    axios.defaults.headers.common = {'Authorization': `Bearer ${user.jwt}`};

    useEffect(() => {
        
        if(file !== null && getFileValidation(file.type.split("/")[0])){
            setIsFileValid(true);
        }
        else{
            setFile(null);
            setPost("");
        }
    }, [file])
    
    function getFileValidation(isType){
        if(isType === 'image'){
            if(file.size > max_img_size){
                alert("File size is too big, max size allowed is 8MB!"); 
                return false;
            }
            else{
                return true;
            }   
        }
        else if(isType === 'video'){
            if(file.size > max_video_size){
                alert("File size is too big, max size allowed is 2GB!");
                return false; 
            }
            else{
                return true;
            }
        }
        else{
            alert(isType + " file type is invalid!");
            return false; 
        } 
    }
    // /api/pages/${pageId}/createPagePost
    function sendCreatePagePostRequest(){
        if(file === null && (post === '')){return alert("Post cannot be empty!");}
        if(post === null || post === ''){return alert("Description can't be empty!");}    
        if((file === null) || ((file !== null) && isFileValid)){
            
            formData.append("description", post);
            if( file !== null)
                formData.append("myFile", file);
                
            axios({url: `/api/pagePosts/page/${pageId}/createPagePost`, method: "POST", data: formData, 
                onUploadProgress: (fileRes) => {
                    const {loaded, total} = fileRes;
                    let myPercent = Math.round((loaded*100) / total);

                    if(myPercent < 100){ setFileUploaded(myPercent); }
                }
            })
            .then((testResponse) => {
                setFileUploaded(100);
                setTimeout(() => { setFileUploaded(0); setFile(null); setPost(""); emitHandlePostClose();}, 1000);
            })
            .catch((error) => {console.log(error);})
        }
    }
    
    return (
        <div>
            <Modal size='lg' show={postShow} onHide={emitHandlePostClose}>
                <Modal.Header className='custom-modalHeader'>
                    <Modal.Title ><h1>Create Page Post</h1> </Modal.Title>
                </Modal.Header>
                <Modal.Body> 
                    
                    <div>
                        <Form.Control className='custom-postTextArea' as='textarea' maxLength={255} placeholder = "Add a description" id='description' onChange={(e) => setPost(e.target.value)} value={post} />
                    </div>
                    <div className="mt-3">
                        <Form.Control type='file' accept='image/*, video/*'  onChange={(e) => {setFile(e.target.files[0]);}} />
                    </div>

                    { fileUploaded > 0 && <ProgressBar striped now={fileUploaded} active="true" label={`${fileUploaded}%`} /> }

                </Modal.Body>
                <Modal.Footer>
                { (fileUploaded === 0) &&
                    <>
                        <Button onClick={() => {sendCreatePagePostRequest();}}> Post </Button>
                        <Button variant="secondary"  onClick={emitHandlePostClose}>
                            Cancel
                        </Button>
                    </>
                }
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default CreatePagePostModal;