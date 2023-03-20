import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import CreatePostModal from '../Modals/CreatePostModal';
import NavBar from '../NavBar/NavBar';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';

const TestDashboard = () => {
    const user = useUser();
    const[show, setShow] = useState(false);
    const[testPost, setTestPosts] = useState([]);

    // function createPost(){
    //     requestToPath("/api/posts", "POST", user.jwt)
    //     .then((postData) => {
    //         console.log(postData);
    //     })
    // }

    useEffect(() => {
        requestToPath("/api/posts/dashboard", "GET", user.jwt)
        .then((postsData) => {
            console.log(postsData);
            setTestPosts(postsData);
        })
    }, [])

    function handleShow(){
        setShow(true);
    }
    function handleClose(){
        setShow(false);
    }

    return (
        <div style={{margin:"0", padding:"0"}}>
            
            {/* apply the bottom classname to everypage */}
            <div className='custom-main'>
                <div style={{display:"flex", justifyContent:"center", alignItems:"center", height:"200px"}}>
                    <Button className='custom-postBtn' onClick={() => {handleShow()}}>Create Post</Button>
                </div>

                <div>
                    {testPost ? testPost.map((allPost) => (
                    <div key={allPost.id} className="mt-5" style={{display:"flex", flexDirection:"column", justifyContent:"center", alignItems:"center",}}>

                        <Card className='custom-postCard'>  
                            <Card.Body>
                                <Card.Title>{allPost.user.firstname} {allPost.user.lastname}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">{allPost.creationDate} change to time collapsed</Card.Subtitle>
                                <Card.Text>
                                    {allPost.description}
                                </Card.Text>
                            </Card.Body>

                            {allPost.postByte !== null ? <>
                            <Card.Img variant="bottom" src={`data:image/jpeg;base64,${allPost.postByte.toString()}`} style={{maxWidth:"100%", maxHeight:"32rem", width:"auto", height:"auto"}}  />
                            </> : <></>}
                        </Card>
                        {/* <img src={`data:image/jpeg;base64,${allPost.postByte.toString()}`} /> */}
                    </div>
                        )) 
                    : (<></>) }
                </div>
                <div style={{textAlign:"center"}}>

                <br/>  All Posts Seen   <br/>  <br/>  <br/><br/><br/>
                </div>
                
            </div>

            <CreatePostModal show={show} emitHandleClose={handleClose} />
            <NavBar/>
        </div>
    );
};

export default TestDashboard;