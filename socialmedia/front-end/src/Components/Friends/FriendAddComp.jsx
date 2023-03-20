import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import FriendRequests from '../../Cards/FriendRequests';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';

import './friends.css';
import SearchUserComp from './SearchUserComp';

const FriendAddComp = (props) => {
    const user = useUser();
    const{userId} = useParams();
    
    const [userList, setUserList] = useState([]);
    const [count, setCount] = useState(0);
    const [searchField, setSearchField] = useState("");
    const [searchList, setSearchList] = useState([]);
    const [isSearch, setIsSearch] = useState(false);

    const default_Null_Status = null;

    function validateStatus(){
        if(userList && userList.length > 0)
            return true;
        else
            return false;
    }

    // gets list of all users in databse,
    // TODO*** Instead getting all the users, only get ones which has similar interests eg. pages, friends

    useEffect(() => {
        requestToPath(`/api/users/notFriend`, "GET", user.jwt)
        .then((usersData) => {
            setUserList(usersData);
            setCount(usersData.length);
        })
        .catch((error) => {console.log(error);})
    },[count])

    function updateCount(){
        const updateCount = count - 1;
        setCount(updateCount);
    }

    function displayAddList(){
        if(userList != null){
            if(validateStatus()){
                return userList.filter((eachUser) => eachUser.id != userId)
                    .sort((a,b) => b.id > a.id ? 1 : -1)
                    .map((eachUser) => {
                        return <div key={eachUser.id}> 
                        <FriendRequests firstname={eachUser.firstname} lastname={eachUser.lastname}
                                        selectedFriendId={eachUser.id} compStatus = {default_Null_Status}  
                                        emitCurrReq={updateCount}          
                        /></div>
                    })
            }
            return <div>No users to Add</div>
        }
    }

    function sendFetchRequestForUser(){
        requestToPath(`/api/users/search?friend=${searchField}`, "GET", user.jwt)
            .then((searchResponse) => {
                setSearchList(searchResponse);
                setIsSearch(true);
            })
            .catch((error) => {console.log(error);})
    }

    function handleClick(){
        sendFetchRequestForUser();
    }

    function handleClear(){
        setIsSearch(false);
        setSearchField("");
    }

    function displayContent(){
        if(searchList != '' && isSearch){
            return searchList.map((eachUser) => {
                return <div key={eachUser.id}><SearchUserComp firstname={eachUser.firstname} lastname={eachUser.lastname} selectedFriendId={eachUser.id} /></div>
            })
        }
        return displayAddList();
    }

    return (
        <div>
            <div className='d-flex justify-content-center mt-5'>
                <input placeholder='name or email' onChange={(e) => setSearchField(e.target.value)} value={searchField} />
                <Button onClick={handleClick}>Search</Button>
                <Button variant='danger' hidden={!isSearch} onClick={handleClear} >Clear</Button>
            </div>
            <div className='container friendLC-RC-wrapper mt-5 mb-5'>
                
                <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Add Friends</h2>
                <div className="friendLC-RC-eachFriend justify-content-center" >
                    {displayContent()}
                </div>
            </div>
        </div>
        
    );
};

export default FriendAddComp;