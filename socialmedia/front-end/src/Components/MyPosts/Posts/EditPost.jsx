import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { Button, Card, Form, ProgressBar } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

const EditPost = () => {
    const user = useUser();
    const {postId, userId} = useParams();
    const formData = new FormData();
    const navigate = useNavigate();

    const [myPost, setMyPost] = useState({postByte: null, posts: {}});
    const [imgPrev, setImgPrev] = useState(null);
    const emptyFile = null;
    const [file, setFile] = useState(emptyFile);
    const [fileUploaded, setFileUploaded] = useState(0);
    const prevMyPost = useRef(myPost);

    axios.defaults.headers.common = {'Authorization': `Bearer ${user.jwt}`};

    const {posts} = myPost;
    const currPostId = posts.id;
    const currPostCreatedOn = posts.creationDate;
    const currPostDesc = posts.description;
    
    useEffect(() => {
        requestToPath(`/api/posts/${postId}/postPhotos`, "GET", user.jwt)
            .then((postResponse) => { setMyPost(postResponse); })
            .catch((error) => {console.log(error);})
    }, [])

    useEffect(() => {
        fetch(`/api/posts/${postId}/postImages`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
        .then((dataBuffer) => { return dataBuffer.blob(); })
        .then((data) => { 
            setImgPrev(URL.createObjectURL(data));
            return data;
        })
        .catch((error) => {console.log(error);})
    }, [])

    useEffect(() => {
        if(prevMyPost.current.posts.description === undefined)
            prevMyPost.current = myPost;
    }, [myPost])
  
    function updateMypost(prop, value){
        setMyPost(prevState => ({
            ...prevState, posts: {
                ...prevState.posts,
                [prop]: value,
            }
        }));
    }

    function handleFile(e){
        setFile((e.target.files[0]));
        setImgPrev(URL.createObjectURL(e.target.files[0]));
    }

    function sendUpdatedPost(){
        
        if(file !== null && (file.size > 52428800)){
            setFile(null);
            return alert("File size is too big, max size allowed is 50MB!");
        }
        if(prevMyPost.current.posts.description !== myPost.posts.description){
            formData.append("description", myPost.posts.description);         
        }
        if(file !== null){
            formData.append("myFile", file);
        }
        
        axios({ url: `/api/posts/${postId}`, method: "PUT", data: formData,
            onUploadProgress: (fileRes) => {
                const {loaded, total} = fileRes;
                let myPercent = Math.round((loaded*100)/total);

                if(fileUploaded < 0){ setFileUploaded(myPercent); }
            }
        })
            .then((fileResponse) => {
                setFileUploaded(100);
                setTimeout(() => {
                    setFileUploaded(0); 
                    navigate(`/users/${userId}/myPosts`);
                    setFile(emptyFile);
                    setImgPrev(emptyFile);
                }, 1000);
            })
            .catch((error) => {console.log(error);})
    }

    function displayFile(){
        if(file != null){
            return <div className='d-flex justify-content-center'><Card.Img fluid="true" src={imgPrev} /></div>
        }
        if(imgPrev != null && file == null){
            return <div className='d-flex justify-content-center'><Card.Img  variant="bottom" src={imgPrev}
            style={{width:"50%", height:"auto"}}  /></div>
        }
    }

    function displayUpload(){
        return <Form.Control className='mt-1' type='file' accept='image/*, video/*' onChange={(e) => {handleFile(e);}}/>;
    }

    function displayProgressBar(){
        if(fileUploaded > 0){
            return <ProgressBar className='mt-2' striped now={fileUploaded} active="true" label={`${fileUploaded}%`} />;
        }
    }

    function displayBtn(){
        if(fileUploaded == 0){
            return <div className='d-flex justify-content-between' style={{padding: "0 1rem 1rem 1rem"}}>
                        <Button variant='outline-primary' onClick={() => {sendUpdatedPost();}}>Save</Button> 
                        <Button variant='outline-danger' onClick={() => {navigate(`/users/${userId}/myPosts`)}}>Back</Button>
                    </div>
        }
    }

    function displayContent(){
        if(myPost.posts == null){
            return <></>
        }

        return <Card className='custom-postCard'>
                    <Card.Body>
                        <Card.Text>
                            <Form.Control className='custom-postTextArea' 
                                as='textarea' 
                                maxLength={255} 
                                placeholder = "Add a description" 
                                id='description' 
                                onChange={(e) => {updateMypost("description", e.target.value);}} value={currPostDesc} />
                        </Card.Text>
                        <div style={{margin:"auto", width: "50%"}}>
                            {displayFile()}
                            {displayUpload()}
                            {displayProgressBar()}
                        </div>
                    </Card.Body>
                    {displayBtn()}     
        </Card>
    }

    return (
        <div className='mt-5 custom-cardUpost ps-1 pe-1'>
            {displayContent()}
        </div>
    );
};

export default EditPost;