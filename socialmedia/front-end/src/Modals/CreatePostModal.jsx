import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, ProgressBar } from 'react-bootstrap';
import { useUser } from '../UserProvider/UserProvider';

const CreatePostModal = (props) => {
    const user = useUser();
    const formData = new FormData();
    const {show, emitHandleClose} = props;
    
    const emptyPost = "";
    const [post, setPost] = useState(emptyPost);
    const [file, setFile] = useState(null);
    const [fileUploaded, setFileUploaded] = useState(0);
    const [isFileValid, setIsFileValid] = useState(false);

    const postBody = post;
    
    // init file size
    const max_img_size = 8388608; // 8mb
    const max_video_size = 209715200; // 200mb
    axios.defaults.headers.common = {'Authorization': `Bearer ${user.jwt}`};

    useEffect(() => {
        if(file !== null && getFileTypeValidation(file.type.split("/")[0])){         
            setIsFileValid(true);
        }
        else{
            setFile(null);
            setPost(emptyPost);
        }
    },[file]);

    function getFileTypeValidation(isType){

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
                alert("File size is too big, max size allowed is 200MB!");
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

    function CreatePost(){
    
       if(file === null && (post === '')){
            return alert("Post cannot be empty!");
       }
       if(post === null || post === ''){
            return alert("Description can't be empty!");
       }    
        if((file === null) || ((file !== null) && isFileValid)){
            formData.append("description", postBody);
            if(file !== null)
                formData.append("myFile", file);    

            axios({url: `/api/posts`, method: "POST", data: formData,
                onUploadProgress: (fileRes) => {
                    const {loaded, total} = fileRes;
                    let myPercent = Math.round((loaded*100)/total);

                    if(myPercent < 100){ setFileUploaded(myPercent); }
                }
            })
                .then((fileResponse) => {
                    setFileUploaded(100);
                    setTimeout(() => {setFileUploaded(0); setFile(null); setPost(emptyPost); emitHandleClose();}, 1000);
                    window.location.reload();
                })
                .catch((error) => {console.log(error);})
        }     
    }

    return (
        <div>
            <Modal size='lg' show={show} onHide={emitHandleClose}>
                <Modal.Header className='custom-modalHeader'>
                    <Modal.Title ><h1>Create Post</h1> </Modal.Title>
                </Modal.Header>
                <Modal.Body>                     
                    <div>
                        <Form.Control required className='custom-postTextArea' as='textarea' maxLength={255} placeholder = "Add a description" id='description' onChange={(e) => setPost(e.target.value)} value={post} />
                    </div>
                    <div className="mt-3">
                        <Form.Control type='file' accept='image/*, video/*' onChange={(e) => {setFile(e.target.files[0]); }} />
                    </div>

                    { fileUploaded > 0 && <ProgressBar striped now={fileUploaded} active="true" label={`${fileUploaded}%`} /> }
                </Modal.Body>
                <Modal.Footer>
                {fileUploaded === 0 && <>

                    <Button onClick={() => {CreatePost();}}> Post </Button>
                    <Button variant="secondary" onClick={emitHandleClose}>
                        Cancel
                    </Button>
                </>}
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default CreatePostModal;