import React, { useState } from 'react';
import { useEffect } from 'react';
import { Button, Card, Form, ProgressBar } from 'react-bootstrap';
import {  useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../../UserProvider/UserProvider';
import requestToPath from '../../Service/fetchService';
import axios from 'axios';

const EditPosts = () => {
    const user = useUser();
    const {pageId, postId, userId} = useParams();
    const formData = new FormData();
    const navigate = useNavigate();

    const [currPost, setCurrPost] = useState({pagePost: {}, postByte: null });
    const [imgPrev, setImgPrev] = useState("");
    const [file, setFile] = useState(null);
    const [fileUploaded, setFileUploaded] = useState(0);

    axios.defaults.headers.common = {'Authorization': `Bearer ${user.jwt}`};
    // get the data of selected posts
    useEffect(() => {
        // /api/pages/${pageId}/posts/${postId}
        requestToPath(`/api/pagePosts/page/${pageId}/posts/${postId}`, "GET", user.jwt)
            .then((postResponse) =>{
                setCurrPost(postResponse);
                setImgPrev(postResponse.postByte);
            })
            .catch((error) => {console.log(error);})
    }, [])

    // function allows to update the selected post
    function updatePagePost(prop, value){
        const copyPPost = {...currPost};
        copyPPost[prop] = value;
        setCurrPost(copyPPost);
    }

    // function to handle event, sets file and allows to set prevImg
    function handleFile(e){
        setFile((e.target.files[0]));
        setImgPrev(URL.createObjectURL(e.target.files[0]));
    }

   // function allows to update the selected post 
   function sendUpdatePagePostRequest(){
        const postBody = currPost.pagePost.description;
        formData.append("myFile", file);
        formData.append("description", postBody);

        if(!(file.size > 52428800)){
            // /api/pages/${pageId}/posts/${postId}
            axios({url: `/api/pagePosts/page/${pageId}/posts/${postId}`, method: "PUT", data: formData,
                onUploadProgress: (fileRes) => {
                    const {loaded, total} = fileRes;
                    let myPercent = Math.round((loaded*100)/total);
    
                    if(myPercent < 100){ setFileUploaded(myPercent); }
                }
            })
                .then((updateResponse) => {
                    setFileUploaded(100);
                    setTimeout(() => {setFileUploaded(0); navigate(`/users/${userId}/pages/page/${pageId}`);}, 1000);
                })
        }
        else{
            setFile(null);
            alert("File size is too big, max size allowed is 50MB!");
        }
   }

    return (
        <div>
        
        {currPost ? 
            (<div key={currPost.id} className="mt-5 custom-cardUpost">
                <Card className='custom-postCard'>  
                    <Card.Body>
                        <Card.Text>
                            <Form.Control className='custom-postTextArea' as='textarea' maxLength={255} placeholder = "Add a description" id='description' 
                            onChange={(e) => {updatePagePost("description", e.target.value);}} value={currPost.pagePost.description} />
                        </Card.Text>

                        <div style={{margin:"auto", width: "50%"}}>
                            {file !== null ? <>
                                <Card.Img src={imgPrev} />
                            </> : <> 
                            {currPost.postByte ? <><Card.Img variant="bottom" src={`data:image/jpeg;base64,${imgPrev.toString()}`} 
                            style={{maxWidth:"100%", maxHeight:"32rem", width:"auto", height:"auto"}}  /></> : <></> } </>}
                            <br/>
                            <Form.Control type='file' accept='image/*, video/*' onChange={(e) => {handleFile(e);}}/>
                            
                        </div>
                        { fileUploaded > 0 && <ProgressBar striped now={fileUploaded} active="true" label={`${fileUploaded}%`} /> }

                    </Card.Body>
                        {fileUploaded === 0 && 
                        <div className='d-flex justify-content-between p-3'>
                            <Button variant='outline-primary' onClick={() => {sendUpdatePagePostRequest(); }} >Save</Button> 
                            <Button variant='outline-danger' onClick={() => {navigate(`/users/${userId}/pages/page/${pageId}`);}} >Back</Button>
                        </div>
                        }
                </Card>
            
            </div>) : (<div> </div>)

        }
        </div>
    );
};

export default EditPosts;