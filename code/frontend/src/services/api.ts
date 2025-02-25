import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

/**
 * Public routes that don't require authentication
 */
export const getProducts = (): Promise<ProductOverview[]> =>
    api.get('/products')
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

export const getProductById = (id: number) : Promise<ProductInfo> =>
    api.get(`/products/${id}`)
        .then(response => response.data)
        .catch(error =>{ console.log(error); throw error })

export const getProductsByRegionAndPrice = (region: Region | null, minPrice: number | null, maxPrice: number | null): Promise<ProductOverview[]> =>
    api.post('/products/search', { region, minPrice, maxPrice }, { headers: { "Content-Type": "application/json" } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

/**
 * Private routes that require authentication
 */
export const createProduct = (product: ProductCreateAndUpdate, authToken: string): Promise<ProductInfo> =>
    api.post('/products', product, { headers: { "Content-Type": "application/json", "Authorization" : `Bearer ${authToken}` } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

export const updateProduct = (id: number, product: ProductCreateAndUpdate, authToken: string): Promise<ProductInfo> =>
    api.put(`/products/${id}`, product, { headers: { "Content-Type": "application/json", "Authorization" : `Bearer ${authToken}`} })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

export const deleteProduct = (id: number, authToken:string):Promise<ProductInfo> =>
    api.delete(`/products/${id}`, { headers: { "Authorization" : `Bearer ${authToken}` } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

export const getMyProducts = (authToken: string): Promise<SellerProductInfo[]> =>
    api.get('/myDashboard', { headers: { "Authorization" : `Bearer ${authToken}` } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

/**
 * Functions that make requests to the api related with images upload and deletion
 */
export const uploadImage = (image:File, authToken: string): Promise<{id:string, uri:string}> =>
    api.post('/images/upload', image, { headers: { "Content-Type": "multipart/form-data", "Authorization" : `Bearer ${authToken}` } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

export const deleteImage = (id: string, authToken: string): Promise<{ message: string }> =>
    api.delete(`/images/${id}`, { headers: { "Authorization" : `Bearer ${authToken}` } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })

/**
 * User routes
 */
export const register = (email: string, username: string, password: string): Promise<any> =>
    api.post('/user', { email, username, password }, { headers: { "Content-Type": "application/json" } })
        .catch(error => { console.log(error); throw error })

export const login = (email: string, password: string): Promise<any> =>
    api.post('/login', { email, password }, { headers: { "Content-Type": "application/json" } })
        .catch(error => { console.log(error); throw error })

export const logout = (authToken: string): Promise<any> =>
    api.post('/logout', { headers: { "Authorization" : `Bearer ${authToken}` } })
        .catch(error => { console.log(error); throw error })

export const userInfo = (authToken: string): Promise<UserInfo> =>
    api.get('/user', { headers: { "Authorization" : `Bearer ${authToken}` } })
        .then(response => response.data)
        .catch(error => { console.log(error); throw error })